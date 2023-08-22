import { readFileSync } from "fs";
import { StorybookConfig } from "@storybook/html-webpack5";

const config: StorybookConfig = {
  experimental_indexers: [
    {
      test: /.books.js$/,
      index: async (fileName: string) => {
        const code = readFileSync(fileName, { encoding: "utf-8" });
        const lines = code.trim().split("\n").reverse();
        const exportNames: Array<string> = [];
        for (const line of lines) {
          if (line.startsWith("//#")) continue;
          if (!line.includes("Object.defineProperty")) break;
          const match = line.match(
            /Object\.defineProperty\([^,]+,\s*"([^"]+)",/,
          );
          if (!match) {
            console.error(`Failed to parse CLJS export "${line}"`);
            continue;
          }
          const exportName = match[1];
          if (exportName.startsWith("book_")) {
            exportNames.push(exportName);
          }
        }
        return exportNames.reverse().map((exportName) => ({
          type: "story",
          importPath: fileName,
          // title: '', // auto generated from importPath if undefined
          exportName,
          name: exportName.replace(/^book_/, ""),
        }));
      },
    },
  ],
  stories: ["../target/**/*.books.js"],
  addons: ["@storybook/addon-links", "@storybook/addon-essentials"],
  framework: {
    name: "@storybook/html-webpack5",
    options: {},
  },
  docs: {
    autodocs: "tag",
  },
};
export default config;
