<!--
Thanks for contributing a new lexicon!

Before you open this PR, please confirm:
-->

## Locale Information

- **Locale id (BCP-47)**: <!-- e.g. ja-JP, fr-CA -->
- **Display name**: <!-- e.g. 日本語, Français (Canada) -->
- **Direction**: <!-- ltr or rtl -->
- **Contributors**: <!-- @your-github-handle, @reviewer-handle -->

## Translation Coverage

- [ ] `src/main/resources/lexicons/<locale>.json` — every `TODO_TRANSLATE_*` keyword replaced
- [ ] `meta.id` matches the locale id above (not `template-XX-XX`)
- [ ] `src/main/resources/overlays/lsp-ui-texts.json` — UI strings translated
- [ ] `src/main/resources/vocabularies/<locale>-domain.json` — at least one domain vocabulary translated
- [ ] `META-INF/services/aster.core.lexicon.LexiconPlugin` — references the renamed plugin class
- [ ] `META-INF/services/aster.core.identifier.VocabularyPlugin` — same

## Tests

- [ ] `./gradlew test` passes locally
- [ ] Renamed `translationReadinessLexiconId` assertion (or comparable) to assert the new locale id

## Linguistic Review

- [ ] At least one native speaker (not the contributor) has reviewed every translated keyword for naturalness in context (control flow, type names, error messages)
- [ ] Reviewer initials: ____

## Related

- Closes: <!-- aster-cloud/aster-lang-core#... if applicable -->
