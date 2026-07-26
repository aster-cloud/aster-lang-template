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

dependencies {
    // 故意硬编码版本，不使用共享 version catalog（aster-lang-platform，ADR 0012）。
    // 本仓库是给外部贡献者 fork 的独立脚手架——引入 catalog 会让每个 fork 额外
    // 依赖 aster-lang-platform，增加 forker 的搭建负担。catalog 的价值是集中
    // first-party 仓库里散落的多个版本；这里只有一个依赖，字面量更清晰。
    // 升级 core 版本时手动改这一行即可。
    //
    // ★版本 = core 当前从 platform catalog 派生的制品版本（asterLang，见
    // aster-lang-platform/build.gradle.kts）。core 迁到 catalog 派生版本后不再 publish
    // 旧的 0.0.1，此处长期停留 0.0.1 → CI 的 `./gradlew test` 在 Maven Local 找不到
    // aster-lang-core:0.0.1（=2026-06-05 起 template CI 变红的第二层根因；第一层是
    // ci.yml 缺 platform publish）。对齐到 core 当前 catalog 派生版本 1.0.14
    // （1.0.11 从未发布到 Maven Central、CI 侧 checkout 的 core main 实际 publish 1.0.14，
    //  旧 pin 1.0.11 → validate 的 :compileJava 解析不到）。
    implementation("cloud.aster-lang:aster-lang-core:1.0.14")
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
