# aster-lang-template

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

> 15 分钟把 Aster Lang 翻译到你的母语。

## 谁需要这个 repo？

[Aster Lang](https://aster-lang.cloud) 是一个让业务专家用**母语**写策略的 Policy-as-Code 平台。
如果它还没支持你的语种，这个 repo 是你贡献新 lexicon 的起点——无需改 Java 编译器，只需翻译 JSON。

## 15 分钟教程

### 1. 从模板建仓 & rename（2 分钟）

- 用本仓库作为 GitHub template 创建你自己的仓库（不是 fork，也不是裸 clone），
  仓库名取 `aster-lang-<lang>-<region>`：

  ```bash
  gh repo create aster-lang-<lang>-<region> --template aster-cloud/aster-lang-template --public --clone
  ```

  例如：
  - `aster-lang-ja`（日语）
  - `aster-lang-fr-ca`（加拿大法语）
  - `aster-lang-ar`（阿拉伯语）
- 用 IDE 全局 rename：
  - `template` → 你的 lang code（例 `ja`）
  - `TEMPLATE` → 大写形式（例 `JA`）
  - `XX-XX` → IETF BCP 47 ID（例 `ja-JP`）
- rename 必须覆盖下列文件（IDE 全局 rename 会一并改到，但请逐一核对）：
  - `src/main/java/aster/lang/<lang>/<Lang>Plugin.java` 内 3 处**必改**的 `TODO[CONTRIBUTOR]`：
    类名/包名、lexicon 资源路径、`providedLexiconIds()` 返回的 locale id（其余标 TODO 的词汇表/overlay 为可选）
  - `META-INF/services/aster.core.lexicon.LexiconPlugin` 与
    `META-INF/services/aster.core.identifier.VocabularyPlugin`：内容改为重命名后的全限定类名，
    否则 `ServiceLoader` 找不到你的插件
  - `providedLexiconIds()` 与 lexicon JSON 的 `meta.id` 必须一致，测试
    `providedIdsAreConsistentWithCreated` 会校验这一点

### 2. 翻译 lexicon JSON（10 分钟）

打开 `src/main/resources/lexicons/<lang>-<region>.json`，把每个 `TODO_TRANSLATE_*` value 翻译成你的母语。

**翻译原则**：
- ✅ 保留**行业术语**而非通俗词
  - 例：`Module` 译为 `モジュール` 而非 `組`
- ✅ keyword 之间**不重复**（同一 lexicon 内不同 key 不映射到同字符串）
- ❌ 不使用 Aster 语法**保留字符**：`[](),.;:=`
- ❌ 不在 keyword 中包含数字开头字符
- ✅ 多词 keyword 之间用空格分隔（不用下划线）

参考样本（官方语言包集中在 [`aster-lang-locales`](https://github.com/aster-cloud/aster-lang-locales)，一语言一 module）：
- [locales/en/…/lexicons/en-US.json](https://github.com/aster-cloud/aster-lang-locales/blob/main/locales/en/src/main/resources/lexicons/en-US.json)
- [locales/zh/…/lexicons/zh-CN.json](https://github.com/aster-cloud/aster-lang-locales/blob/main/locales/zh/src/main/resources/lexicons/zh-CN.json)
- [locales/de/…/lexicons/de-DE.json](https://github.com/aster-cloud/aster-lang-locales/blob/main/locales/de/src/main/resources/lexicons/de-DE.json)

### 3. 运行 validator（1 分钟）

```bash
./gradlew validateLexicon
```

- 失败 → 按报错提示修复（缺少 keyword / 保留字符 / 重复值 / 非法 meta.id 等）
- 通过 → 进入下一步

### 4. 运行测试（1 分钟）

```bash
./gradlew test
```

`TemplatePluginTest` 只做 **SPI 结构检查**，不跑 sample policy、也不比对 Core IR：

- 插件能实例化，ABI 版本为 `1.x`
- `createLexicon()` 能加载并解析 lexicon JSON，`meta.id` 非空
- `providedLexiconIds()` 包含 `createLexicon().getId()`（id 一致性）
- `getOverlayResources()` 声明的每个 overlay 在 classpath 上都存在
- `createVocabulary()` 能加载至少一个领域词汇表
- 两个 `META-INF/services` 注册文件（`LexiconPlugin`、`VocabularyPlugin`）都存在
- `translationReadinessLexiconId`：断言 id 仍是 `template-XX-XX`——rename 后把期望值改成你的 locale id

翻译内容本身的正确性由上一步的 `validateLexicon` 和母语 reviewer 保证（见 PR 模板）。

### 5. 提交收编申请（1 分钟）

你从模板建出的仓库已经是一个**可独立运行的社区维护语言包**（SPI 自动发现，无需 Aster 介入即可
本地/自有部署加载——见下文"技术细节 → SPI ABI 兼容性"）。如果你希望它被**官方收编**进
[`aster-lang-locales`](https://github.com/aster-cloud/aster-lang-locales)（走"官方背书"路径）：

- 在 [aster-lang-locales Discussions / Issues](https://github.com/aster-cloud/aster-lang-locales/issues)
  发起收编申请，附上你的仓库链接
- 申请用自动模板填充：lang / region / direction / vocabulary 列表 / `validateLexicon` 通过截图
- Aster reviewer **24h** 内首次回复；准入流程见
  [aster-lang-locales README 的"官方收编（Adoption）准入流程"](https://github.com/aster-cloud/aster-lang-locales#官方收编adoption准入流程)

> 不想走官方收编也完全可以：保持"社区维护"路径，用你自己的 maven 坐标发布，
> 在 [docs/community/lexicons](https://aster-lang.dev/community/lexicons) 登记即可被其他用户发现。

## 三条贡献路径

| 路径 | 控制 | 落点 | Aster 介入 | 适用 |
|---|---|---|---|---|
| **官方 lexicon** | Aster team 直接维护 | [`aster-lang-locales`](https://github.com/aster-cloud/aster-lang-locales) 的一个 module | 100% | en/zh/de（核心市场） |
| **官方背书 lexicon** | Community 开发 → Aster review → **晋升**为 `aster-lang-locales` 的新 module | 晋升后进 `aster-lang-locales`（晋升前留在你从模板建出的 repo） | Review + 安全审计 + maven 发布 | 主流语种（ja/fr/es/...） |
| **社区维护 lexicon** | Community 自有 org + 自有 maven coord | 你自己的 repo | 仅 [docs/community/lexicons](https://aster-lang.dev/community/lexicons) 收录 | 长尾语种 / 行业 dialect |

> **关于"官方背书"的落点**：所有 Aster 官方维护的语言包都集中在单一仓库
> [`aster-lang-locales`](https://github.com/aster-cloud/aster-lang-locales)（一语言一 module，不再一语言一 repo）。
> 走"官方背书"路径的语言通过本模板自助开发 + 自测，评审通过后由 Aster team
> **收编**为 `aster-lang-locales` 的新 module。收编准入流程见该仓库 README 的
> "官方收编（Adoption）准入流程"一节。

## 贡献激励

- ✅ **Apache 2.0 license**——你保留贡献者署名权
- ✅ **Aster Language Steward** 标签（合并 ≥ 2 lexicon 或维护 1 lexicon ≥ 12 个月）
- ✅ **¥3,000/年 platform credit**（Steward 限定）
- ✅ 公开 [contributor 名录](https://aster-lang.dev/community/contributors)
- ✅ 优先参与新 SPI ABI 设计讨论

详见 [CONTRIBUTING.md](CONTRIBUTING.md)。

## 技术细节

### 目录结构

```
aster-lang-<lang-region>/
├── LICENSE                       # Apache 2.0
├── NOTICE
├── README.md
├── CONTRIBUTING.md
├── build.gradle.kts              # 依赖 aster-lang-core；必改：artifactId、validateLexicon 的文件名
├── src/main/
│   ├── java/aster/lang/<lang>/
│   │   └── <Lang>Plugin.java     # SPI 实现；必改：3 处 TODO[CONTRIBUTOR]（类名 / 资源路径 / locale id）
│   └── resources/
│       ├── META-INF/services/
│       │   ├── aster.core.lexicon.LexiconPlugin        # SPI 注册；必改：改成重命名后的类名
│       │   └── aster.core.identifier.VocabularyPlugin  # SPI 注册；必改：同上
│       ├── lexicons/
│       │   └── <lang>-<region>.json              # 你翻译这个
│       ├── vocabularies/
│       │   └── <locale>-domain.json              # 领域词汇表（至少一个，PR 模板要求）
│       └── overlays/
│           └── lsp-ui-texts.json                 # 可选：LSP UI 翻译
└── src/test/java/aster/lang/<lang>/
    └── <Lang>PluginTest.java     # 必改：translationReadinessLexiconId 的期望 id
```

### Lexicon JSON 结构

```jsonc
{
  "meta": {
    "id": "ja-JP",                  // IETF BCP 47
    "name": "日本語",                // 该语种自身的名字
    "direction": "LTR"              // LTR 或 RTL
  },
  "keywords": {
    "MODULE_DECL": "モジュール",
    "IMPORT": "使用",
    // ... 所有 keys 必须与 en-US.json 一一对应
  },
  "punctuation": {
    // 以下 5 个键为必填（core DynamicLexicon 解析时 requireText），缺任一项 createLexicon() 抛异常
    "statementEnd": ".",
    "listSeparator": ",",
    "blockStart": ":",
    "stringQuoteOpen": "\"",
    "stringQuoteClose": "\"",
    "enumSeparator": ","             // 可选，缺省取 listSeparator
  }
}
```

### SPI ABI 兼容性

当前 SPI ABI = **v1.0**，承诺至少保证 18 个月不变更（直到 2027-12-01）。
Breaking change 会提前 6 个月通告 + 新旧 ABI 共存一个版本周期。

## 问题反馈

- [GitHub Discussions](https://github.com/aster-cloud/aster-lang-core/discussions)
- [aster-lang.dev/community](https://aster-lang.dev/community)

## License

Apache 2.0 — see [LICENSE](LICENSE).
