import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"

    id("org.flywaydb.flyway") version "9.8.2"
    id("nu.studer.jooq") version "8.1"
}

group = "com.fynnian.dev_meeting"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.postgresql:postgresql")
    jooqGenerator("org.postgresql:postgresql")
    implementation("org.jooq:jooq-postgres-extensions:${jooq.version.get()}")

    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.8.10")
    implementation("com.beust:klaxon:5.5")
}

object DB {
    const val url = "jdbc:postgresql://localhost:7878/dev_meeting"
    const val  user = "dev_meeting"
    const val password = "dev_meeting"
    const val driver = "org.postgresql.Driver"
    const val schema = "dev_meeting"
    val schemas = arrayOf(schema)
}

flyway {
    url = DB.url
    user = DB.user
    password = DB.user
    schemas = DB.schemas
    cleanDisabled = false
}

jooq {
    configurations {
        create("java") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = DB.driver
                    url = DB.url
                    user = DB.user
                    password = DB.password
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = DB.schema
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.fynnian.dev_meeting.jooq.java"
                        directory = "build/generated-src/jooq-java/"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = DB.driver
                    url = DB.url
                    user = DB.user
                    password = DB.password
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = DB.schema
                        excludes = """
                                      flyway_schema_history
                                   """.trimIndent()

                        forcedTypes.addAll(
                            listOf(
                                // currently (3.17) jooq can't handle custom domain types, needs advice for the generator
                                org.jooq.meta.jaxb.ForcedType().apply {
                                    name = "CLOB"
                                    includeTypes = "email"
                                },
                                org.jooq.meta.jaxb.ForcedType().apply {
                                    userType = "com.fynnian.dev_meeting.jooqdevmeeting.OrgStatus"
                                    isEnumConverter = true
                                    includeTypes = "status"
                                },
                                org.jooq.meta.jaxb.ForcedType().apply {
                                    userType = "com.fynnian.dev_meeting.jooqdevmeeting.Translation"
                                    converter = "com.fynnian.dev_meeting.jooqdevmeeting.TranslationConverter"
                                    includeExpression = ".*\\.name_localized"
                                },
                            )
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.fynnian.dev_meeting.jooq.kotlin"
                        directory = "build/generated-src/jooq-kotlin/"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks.withType<nu.studer.gradle.jooq.JooqGenerate>() {
    dependsOn(tasks.withType<org.flywaydb.gradle.task.FlywayMigrateTask>())

    // declare Flyway migration scripts as inputs on the jOOQ task
    inputs.files(project.kotlin.sourceSets["main"].resources.asFileTree)
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    allInputsDeclared.set(true)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


