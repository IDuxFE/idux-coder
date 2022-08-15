import { findTagName, findProp } from '../utils';
import * as vscode from 'vscode';
import { apiMap } from '../data';
import { preference } from '../data/config';
const camelCase = require('lodash.camelcase');
const toUpper = require('lodash.toupper');

const pureTag = /^(.)/;
const pureProp = /(:)(\S+)/;
const pureHover = /@|#/;
const invalidBlock = /script|style/;

export class HoverProvider implements vscode.HoverProvider {
  provideHover(document: vscode.TextDocument, position: vscode.Position): vscode.ProviderResult<vscode.Hover> {
    const wordRange = document.getWordRangeAtPosition(position);

    if (!wordRange) {
      return null;
    }

    const hoveredWord = document.getText(wordRange);

    let tagName = findTagName(document, position, hoveredWord) ?? '';

    if (hoveredWord.length < 100 && !invalidBlock.test(tagName)) {
      // TODO: fix pref problem
      const { prefix, lang } = preference;

      const propName = findProp(document, position, hoveredWord);
      const hoverType = hoveredWord.indexOf('#') >= 0 ? 'slots' : 'props';

      if (!propName && !tagName) {
        tagName = hoveredWord;
      }

      // fix: hover native html element
      if (!prefix.test(tagName)) {
        return null;
      }

      // convert any-case to pascal-case
      const pureTagName = camelCase(tagName!.replace(prefix, '')).replace(pureTag, toUpper);

      // convert any-case to camel-case
      const pureHoverName = camelCase(hoveredWord.replace(pureHover, ''));

      let tempMap = apiMap[pureTagName];
      let queryResult: string = '';

      if (!tempMap) {
        return null;
      }

      tempMap = tempMap[lang];

      if (!propName && hoveredWord === tagName) {
        queryResult = tempMap.raw;
      } else {
        tempMap = tempMap[hoverType];

        // convert any-case to camel-case
        const purePropName = (propName?.[0] ?? '').replace(pureProp, (_, b, c) => b + camelCase(c));

        queryResult = tempMap[pureHoverName] || tempMap[purePropName];
      }
      return new vscode.Hover(queryResult);
    }
    return null;
  }
}
