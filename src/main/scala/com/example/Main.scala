package com.example

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

//To re-generate slef-signed cert use.
//keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048
//in your browser: https://localhost:8443  ( click vist website, you need to accept slef-cigned cert first time )

case class Device(id: Int, model: String)
case class User(name: String, devices: Seq[Device])

object Main extends IOApp {

  given codec: JsonValueCodec[User] = JsonCodecMaker.make

  val R: HttpRouteIO = {
    case GET -> Root / "test" => IO(Response.Ok())
    //////////////////////////////
    case GET -> Root / "json" =>
      for {
        json <- IO(
          writeToArray(
            User(
              name = "John",
              devices = Seq(Device(id = 2, model = "iPhone X"))
            )
          )
        )
      } yield (Response
        .Ok()
        .asStream(Stream.chunk(Chunk.array(json)))
        .contentType(JSON))

    ///////////////////////////////    
    // Body POST example: { "name" : "John", "devices" : [{"id":1,"model":"HTC One X"}] }
    case req @ POST -> Root / "user" =>
      for {
        payload <- req.body
        user <- IO(readFromArray(payload))
        _ <- Logger[IO].info( "json-template-gh2: user: " +  user.toString())

      } yield (Response.Ok().asText(user.name + " - accepted"))
  }

  def run(args: List[String]): IO[ExitCode] =
    for {
      ctx <- QuartzH2Server.buildSSLContext("TLS", "keystore.jks", "password")
      //exitCode <- new QuartzH2Server( "0.0.0.0", 8443, 16000, Some(ctx)).iouring_startIO(R, urings = 1)
      exitCode <- new QuartzH2Server( "0.0.0.0", 8443, 16000, Some(ctx)).startIO(R, sync = false)
  

    } yield (exitCode)
}
