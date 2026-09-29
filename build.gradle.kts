// Aster Lang lexicon repo build file (template)
//
// TODO[CONTRIBUTOR]:
//   1. 改 group / artifactId 为你的语种 ID
//   2. 改 publishing.repositories 为你的 maven 仓库（或保留 mavenLocal 用于开发）
//   3. R21 (audit): 模板版本号与已发布的 sibling lexicons (en/de/zh) 保持
//      一致, 避免 SNAPSHOT 浮动版本意外被纳入消费者构建.
plugins {
    `java-library`
    `maven-publish`
}

group = "cloud.aster-lang"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

// aster-lang-core 的版本不是字面量，而是由 aster-lang-platform 版本目录（ADR 0012）派生，
// 随 core main 持续前进。本仓故意不引入该 catalog——它是给外部贡献者建仓用的独立脚手架，
// 多一个 platform 依赖只会增加搭建负担。因此版本改由外部注入：CI 把 core 实际
// publishToMavenLocal 的版本通过 -PasterLangCoreVersion 传进来，永远与 CI 侧一致；
// 本地开发不传时用下面的回退值（须是本机 ~/.m2 或远程仓库里存在的版本）。
val asterLangCoreVersion = providers.gradleProperty("asterLangCoreVersion").orElse("1.0.30")

dependencies {
    implementation("cloud.aster-lang:aster-lang-core:${asterLangCoreVersion.get()}")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.0")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            // TODO[CONTRIBUTOR]: 改 artifactId 为 aster-lang-<lang-region>
            artifactId = "aster-lang-template"
        }
    }
}

// 自定义校验任务：用 aster-lang-core 提供的 LexiconValidatorCli 校验本仓库的 lexicon JSON
tasks.register<JavaExec>("validateLexicon") {
    description = "Validate this lexicon JSON against Aster SPI requirements"
    group = "verification"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("aster.core.lexicon.tools.LexiconValidatorCli")
    // TODO[CONTRIBUTOR]: 改文件名为 你的 lang-region
    args = listOf("src/main/resources/lexicons/template-XX-XX.json")
}
