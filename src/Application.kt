package com.example

import com.example.article.Comment
import com.example.article.articlePage
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import com.fasterxml.jackson.databind.*
import htmlDsl
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.util.pipeline.*
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
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
                testPage += newPage;

                call.respond(
                    mapOf(
                        "ok" to true
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
                            "page" to requestPage
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

