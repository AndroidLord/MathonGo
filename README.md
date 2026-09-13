# Quiz Answer

A native Android question-attempt screen for JEE Advanced previous-year questions, built with Kotlin
and Jetpack Compose. Questions are read from a bundled `data.json`; nothing is fetched from a
backend and no question, option, answer or ID is hardcoded.

## Features

- Navigation shell: Home (subject tabs + chapters) -> Chapter (question list) -> Question attempt
- Light/dark theme toggle on Home, remembered across launches
- 599 questions across 3 subjects and 21 chapters, in the original JSON order
- Three question types: `singleCorrect`, `multipleCorrect`, `numerical`
- Four answer states per question: unanswered, selected, checked-correct, checked-incorrect
- The correct answer is never revealed before Check Answer is pressed
- Rich question and option content: HTML, entities, tables, images, LaTeX and MathML
- Previous/Next navigation with per-question answer state that survives navigation and rotation
- Light and dark themes, including inside the rich-content renderer
- Exam/chapter breadcrumb, question number, previous-year paper label, video-solution indicator
- Error screen with retry when the data cannot be loaded

## Tech stack

| | |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose, Material 3 (Compose BOM 2026.02.01) |
| Build | AGP 9.3.2 (built-in Kotlin), Gradle 9.5 |
| DI | Hilt 2.60.1 with KSP |
| Navigation | Navigation Compose 2.10.1, type-safe routes |
| JSON | kotlinx.serialization 1.9.0 |
| Math/HTML | WebView + bundled MathJax 3.2.2, androidx.webkit 1.14.0 |
| Min / Target SDK | 24 / 37 |

## Project structure

```
core/util/          AnswerChecker — answer validation, no Android dependencies
data/local/         ExamDto (JSON models), AssetQuestionRepository, ThemePreferences
data/mapper/        QuestionMapper — DTO to domain, flatten and copy context
domain/model/       Question, Option, NumericalAnswer, QuestionBank, QuestionType
domain/repository/  QuestionRepository interface
di/                 Hilt modules, dispatcher qualifier, QuestionFlowConfig
presentation/navigation/
                    Routes (type-safe), AppNavHost
presentation/catalog/
                    CatalogViewModel, HomeScreen, ChapterScreen, CatalogCommon
presentation/theme/ ThemeViewModel
presentation/question/
                    QuestionScreen, QuestionViewModel, QuestionUiState,
                    QuestionAnswerState, QuestionHeader, QuestionContent,
                    OptionCard, NumericalAnswerInput, BottomActions
rendering/          RichContent, MathContentWebView, RichContentTemplate, HtmlText
ui/theme/           Theme, ThemeMode, FeedbackPalette, OptionColors
```

## Navigation

`AppNavHost` uses Navigation Compose with `@Serializable` type-safe routes:

```
HomeRoute -> ChapterRoute(chapterId) -> QuestionRoute(chapterId, startIndex)
```

Home presents subjects as tabs and lists the selected subject's chapters, so choosing a subject and
a chapter happens on one screen. Chapter lists that chapter's questions with a plain-text preview,
its previous-year label and a video indicator. Both read from the same parsed `QuestionBank`;
nothing is hardcoded. The question screen reads `chapterId` and `startIndex` from `SavedStateHandle`
via `toRoute<QuestionRoute>()`, scopes the flow with `QuestionBank.inChapter(chapterId)` and opens
on the question that was tapped.

`AssetQuestionRepository` is a `@Singleton` and caches the parsed bank behind a `Mutex`, so moving
between screens does not re-parse the 2.2 MB file.

## State management

`QuestionViewModel` owns the question list, the current index, every per-question answer record,
navigation and validation. Composables only render state and emit actions.

Two representations are kept deliberately separate:

- `AnswerRecord(selectedOptionIds, numericalInput, checked)` is what gets **persisted**. It is
  `@Serializable` and type-agnostic.
- `QuestionAnswerState` is **derived** and models the four states as a sealed interface:
  `Unanswered`, `Selected`, `CheckedCorrect`, `CheckedIncorrect`. Illegal combinations such as
  "checked with nothing selected" cannot be represented, and `canCheck` is simply
  `answerState is Selected`.

Answer records are keyed by `question.id` (the `_id.$oid` from the JSON), never by list index, and
are mirrored into `SavedStateHandle` as JSON — so they survive configuration changes and process
death. The current index is held in `SavedStateHandle` directly.

## Theming

Three modes are supported (`ThemeMode.SYSTEM/LIGHT/DARK`). The Home app bar carries a
sun/moon toggle that flips between light and dark; the choice is stored in `SharedPreferences` by
`ThemePreferences` and survives app restarts. Dark is the default on first launch.

Answer feedback colours live in a `FeedbackPalette` with separate light and dark values, exposed to
Compose through `LocalIsDarkTheme`, and are injected into the rich-content WebView so HTML and
maths follow the same theme. Checked option cards keep the plain card fill; only the border, the
letter badge and the floating state label carry colour.

## Data handling

`data.json` nests `exam -> subjects -> chapters -> questions -> options`. `QuestionMapper` flattens
this into a single `List<Question>` in source order, copying the exam, subject and chapter titles
onto each question so the UI never walks back up the tree.

Notes on the real file, which differs from what a schema might suggest:

- `_id` is an object wrapper, `{"$oid": "..."}`, not a bare string
- `previousYearPapers` is a `List<String>` of labels, not objects
- `question.image` and `option.image` exist but are **null on every record** — all images are
  `<img>` tags inside the HTML text
- `correctValue` is a JSON **string** on 188 numerical questions and a JSON **number** on 12, so it
  is deserialized as `JsonElement` and coerced
- Chapter `order` is non-contiguous and is deliberately ignored to preserve JSON ordering

Parsing runs on `Dispatchers.IO` via `Json.decodeFromStream`, so the 2.2 MB file is never held as a
single `String`. `loadQuestions()` returns `Result` and never throws; a malformed or missing file
surfaces as the error screen.

Which question types appear in the flow is injected via `QuestionFlowConfig`, not hardcoded.

## Rich content rendering

Question and option content is a mix of plain text, HTML, entities, tables, images, LaTeX and
MathML — often several in one string. One reusable renderer handles all of it:

```
Compose -> AndroidView -> WebView -> local HTML template -> MathJax
```

Only question and option content uses a WebView. The rest of the app is native Compose.

- `RichContentTemplate` loads `assets/rich_content.html` and injects the current theme's text,
  link and border colors plus font size, so HTML and math match light/dark mode.
- `MathContentWebView` hosts the WebView, sizes it from a height reported by JavaScript after
  MathJax typesetting completes, and serves local assets through `WebViewAssetLoader`.
- Option content is rendered with touch disabled so taps reach the card underneath.
- Wide tables are wrapped in a horizontally scrolling container; images are capped at 100% width;
  images that fail to load are hidden rather than left as broken boxes.

## MathJax setup

MathJax 3.2.2 is bundled at `assets/mathjax/tex-mml-svg.js` and served over
`https://appassets.androidplatform.net/assets/` by `WebViewAssetLoader`. No CDN is used.

The **SVG** output build is used deliberately: it embeds its own font data, so rendering needs no
font files from the network. Both input formats are enabled — LaTeX (`$...$`, `\(...\)`) and
MathML (`<math>...</math>`). Markup is never stripped or replaced with plain text.

## Error handling

- Missing or malformed `data.json`, or a file with no usable questions: error screen with retry
- Missing/null optional metadata: the element is hidden, never rendered as "null"
- Unrecognised question types map to `UNKNOWN` instead of failing the load
- Broken image URLs: the image is hidden, the rest of the question still renders
- WebView render-process death is caught in `onRenderProcessGone`, which would otherwise kill the
  app; the content falls back to plain text with entities decoded
- If WebView is unavailable on the device at all, the same plain-text fallback is used
- Numerical input is filtered to a numeric pattern and unparseable values are simply wrong

## Build requirements

- JDK 17 or newer (Android Studio's bundled JBR works)
- Android SDK with API 37
- Gradle wrapper is included; no local Gradle install needed

## Build instructions

```bash
./gradlew clean
./gradlew assembleDebug
```

If `./gradlew` reports "Unable to locate a Java Runtime", point `JAVA_HOME` at a JDK first.

## Test instructions

There is no automated test suite in this repository; verification was done by running the app
against the real `data.json`.

## APK location

```
app/build/outputs/apk/debug/app-debug.apk
```

## Known limitations

- No automated tests
- Video solutions show an indicator only; playback is not implemented
- `multipleCorrect` and `numerical` reuse the single-correct check flow; there is no partial credit
- Answers are not persisted across app restarts, only across configuration changes and process death
  within a session
- Home and Chapter are intentionally light navigation shells
- Chapter rows show the total question count; attempt progress is not tracked across sessions
- Answer state is per question id, so it is shared if the same question is reached from elsewhere
- Images render at their intrinsic size up to the available width; low-resolution source images are
  not upscaled, so some diagrams appear small
- `android.disallowKotlinSourceSets=false` is set in `gradle.properties` because KSP registers its
  generated sources in a way AGP 9's built-in Kotlin rejects by default. This flag is marked
  experimental by AGP and may need revisiting on a future version.
- The debug APK is ~19 MB, of which ~2 MB is MathJax and ~2 MB is `data.json`
