import fs from "node:fs";
import postcss from "postcss";
import selectorParser from "postcss-selector-parser";

const [inputPath, outputPath, wrapper = ".tailwind-wrapper"] = process.argv.slice(2);

if (!inputPath || !outputPath) {
    console.error("Usage: node scope-ces-theme-css.mjs <input> <output> [wrapper]");
    process.exit(1);
}

const source = fs.readFileSync(inputPath, "utf8");
const root = postcss.parse(source, {from: inputPath});

root.walkAtRules("tailwind", atRule => atRule.remove());
root.walkAtRules("layer", atRule => atRule.replaceWith(...(atRule.nodes ?? [])));

root.walkRules(rule => {
    for (let parent = rule.parent; parent; parent = parent.parent) {
        if (parent.type === "atrule" && /keyframes$/i.test(parent.name)) return;
    }

    rule.selector = selectorParser(selectors => {
        selectors.each(selector => {
            if (selector.toString().trim() === "html") {
                selector.removeAll();
                selector.append(selectorParser.className({value: wrapper.replace(/^\./, "")}));
                return;
            }

            selector.prepend(selectorParser.combinator({value: " "}));
            selector.prepend(selectorParser.className({value: wrapper.replace(/^\./, "")}));
        });
    }).processSync(rule.selector);
});

const banner = `/* Generated from ${inputPath}. Run make generate-tailwind-wrapper-css to update. */\n`;
fs.writeFileSync(outputPath, banner + root.toString().trim() + "\n");
