package com.example

import IdAlreadyExitExcetion
import IdNotFoundException
import Comment
import com.example.article.articlePage
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import com.fasterxml.jackson.databind.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import htmlDsl
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.util.pipeline.*
import org.jetbrains.exposed.sql.Database
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads

fun initdatabase(){
    val config = HikariConfig("/hikari.properties");
    config.schema = "public"
    val dataSourse = HikariDataSource(config);
    Database.connect(dataSourse);
}

fun Application.module(testing: Boolean = false) {
    initdatabase()
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(StatusPages){
        exception<Throwable>{
            call.respond(HttpStatusCode.InternalServerError)
        }
        exception<com.fasterxml.jackson.core.JsonParseException>{
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException>{
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<IdAlreadyExitExcetion>{
            call.respond(HttpStatusCode.BadRequest)
      }
        exception<IdNotFoundException>{
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    val testPage = Collections.synchronizedList(mutableListOf(
       articlePage(
           "1",
           "problem",
           "A == B ?",
           0,
           1,
           Date(),
           listOf(
              Comment(
              1,
              "owo",
            "zanzan",
            Date(),
            1,
            0,
            )
           )
       )
    ));
    val client = HttpClient(Apache) {
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
        route("/page"){
            get {
                val page = testPage.map {
                    mapOf(
                        "id" to it.id,
                        "title" to it.title,
                    )
                }
                call.respond(mapOf(
                    "data" to page
                    ))
              }
            post {
                val newPage = call.receive<articlePage>();
                if(testPage.any{ it.id == newPage.id}){
                    throw IdAlreadyExitExcetion()
                }

                testPage += newPage;

                call.respond(
                    mapOf(
                        "OK" to true
                    )
                )
            }
            route("/{id}"){
                get{
                    val requestId = call.parameters["id"]
                    val requestPage = testPage.firstOrNull(){
                        it.id == requestId
                    };
                    call.respond(
                        mapOf(
                            "page" to (requestPage ?: throw IdNotFoundException())
                        )
                    )
                }
                put{
                    val requestId = call.parameters["id"]
                    testPage.removeIf{
                        it.id == requestId
                    }
                    val updatePage = call.receive<articlePage>()
                    testPage += updatePage
                    call.respond(
                        mapOf(
                            "ok" to true
                        )
                    )
                }
                delete{
                    val requestId = call.parameters["id"]
                    testPage.removeIf{it.id == requestId}

                    call.respond(
                        mapOf(
                            "ok" to true
                        )
                    )
                }
            }
        }
        get("/html-dsl") {
            htmlDsl();
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

