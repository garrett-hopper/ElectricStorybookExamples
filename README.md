# Electric Storybook Example
This repo implements a `defbook` which can be used with [Electric](https://github.com/hyperfiddle/electric) code to create [Storybook](https://storybook.js.org) stories.

See [storybook.clj](./src/utils/storybook.clj) for implementation.

The macro takes in the format

```clojure
(defbook ...StoryName... [...optional arg bindings...]
  ...electric code...)
```

and converts it to an exported JS object containing a `render` function which will mount the Electric program as a Storybook test.

Additionally, it supports Storybook `parameters`, `args`, and `argTypes` through metadata.

NOTE: This currently depends on a yet unmerged fix to Storybook which prevents non-`^:export` defs from being seen by Webpack (and thus seen by Storybook as stories): [shadow-cljs/pull/1143](https://github.com/thheller/shadow-cljs/pull/1143)

# Steps

1. Copy the `defbook` macro (`utils.storybook` NS in [storybook.clj](./src/utils/storybook.clj) & [storybook.cljs](./src/utils/storybook.cljs)) somewhere
2. Copy the [.storybook](./.storybook) directory
3. Add the `dependencies` from [package.json](./package.json)
4. Use the `defbook` macro in a `.books` NS. (Specified by [.storybook/main.ts](./.storybook/main.ts); `story`/`stories` conflicts with Storybook's default CSF indexer.)
5. Start Dev REPL and run `(user/main)` (This starts Shadow-CLJS `:books` watch process and Electric server.)
6. Start Shadow-CLJS with `npm run storybook`

See [books.cljs](./src/app/books.cljc) for example macro usage
