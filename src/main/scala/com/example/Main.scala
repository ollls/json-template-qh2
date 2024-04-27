package com.example

import ch.qos.logback.classic.Level
import cats.effect.{IO, IOApp, ExitCode}
import io.quartz.QuartzH2Server
import io.quartz.http2.routes.{HttpRouteIO, Routes}
import io.quartz.http2.model.{Headers, Method, ContentType, Request, Response}
import io.quartz.http2._
import io.quartz.http2.model.Method._
import io.quartz.http2.model.ContentType.JSON

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import fs2.{Stream, Chunk}

import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.quartz.MyLogger._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.jsoniter.jsonBody
import io.quartz.sttp.QuartzH2ServerInterpreter

//To re-generate slef-signed cert use.
//keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048
//in your browser: https://localhost:8443  ( click vist website, you need to accept slef-cigned cert first time )

case class Device(id: Int, model: String)
case class User(name: String, devices: Seq[Device])

object Main extends IOApp {

  given codec: JsonValueCodec[User] = JsonCodecMaker.make

   def run(args: List[String]): IO[ExitCode] =
    val top = endpoint.get
      .in("")
      .errorOut(stringBody)
      .out(stringBody)
      .serverLogic(Unit => IO(Right("ok")))
    val user =
      endpoint.get
        .in("user")
        .errorOut(stringBody)
        .out(jsonBody[User])
        .serverLogicSuccess(Unit =>
          IO(new User("Olaf", Array(new Device(15, "bb15"))))
        )
    val serverEndpoints = List(top, user)

    val TAPIR_ROUTE = QuartzH2ServerInterpreter().toRoutes(serverEndpoints)

    for {
      _ <- IO(QuartzH2Server.setLoggingLevel(Level.TRACE))
      ctx <- QuartzH2Server.buildSSLContext("TLS", "keystore.jks", "password")
      exitCode <- new QuartzH2Server("0.0.0.0", 8443, 16000, ctx)
        .start(TAPIR_ROUTE, sync = false)

    } yield (exitCode)
}
