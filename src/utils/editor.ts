import { Position, TextDocument } from 'vscode';

export const commentLabel = /(^<!--|-->$)/;

const singleLineLabel = /(^<[\w|-]+).*(\/.*>$)/;
const label = /^<([\w|-]+)/;
const tagStart = /^<([\w|-]+)/;
const tagEnd = /^>?<\/([\w|-]+)>/;
const tagProps = /\s*([^\s"'<>\/=]+)(?:\s*=\s*(?:"([^"]*)"+|'([^']*)'+|([^\s"'=<>`]+)))/g;

/**
 * extract the prop k-v
 * @param lineText
 * @param propValue
 * @returns
 */
export const findProp = (doc: TextDocument, pos: Position, propValue: string) => {
  const lineText = doc.lineAt(pos.line).text.trim();

  let m;
  while ((m = tagProps.exec(lineText)) !== null) {
    // This is necessary to avoid infinite loops with zero-width matches
    if (m.index === tagProps.lastIndex) {
      tagProps.lastIndex++;
    }

    if (m[0].includes(propValue)) {
      // reset regex /g state
      tagProps.lastIndex = 0;
      return [m[1], m[2]];
    }
  }
  return null;
};

/**
 * find current tag's name
 * ```
 * <Table>
 *  <template hoverEl>
 *  </template>
 * </Table>
 * // hover hoverEl
 * // return hoverEl
 * ```
 * @param doc TextDocument
 * @param pos Position
 * @returns
 */
export const findTagName = (doc: TextDocument, pos: Position, hoveredWord: string) => {
  let curLine = pos.line;

  if (hoveredWord.indexOf('#') >= 0) {
    return findSlotParent(doc, curLine);
  }

  while (curLine >= 0) {
    const lineText = doc.lineAt(curLine).text.trim();

    if (commentLabel.test(lineText)) {
      curLine--;
    } else if (label.test(lineText)) {
      return lineText.match(label)![1];
    } else {
      if (tagStart.test(lineText) || tagEnd.test(lineText)) {
        return null;
      }

      curLine--;
    }
  }
  return null;
};

/**
 * find current tag's parent
 * ```
 * <Table>
 *  <template <!#hover>></template>
 * </Table>
 * // hover: <!#hover/>
 * // return: Table#hover
 * ```
 * @param doc TextDocument
 * @param curLine
 */
export const findSlotParent = (doc: TextDocument, curLine: number) => {
  curLine--; // avoid to traverse hover line

  let noChildTag = false; // flag <Ix \n\n\n />
  let level = 0;

  while (curLine >= 0) {
    const lineText = doc.lineAt(curLine).text.trim();

    if (singleLineLabel.test(lineText) || commentLabel.test(lineText)) {
      curLine--;
    } else {
      if (lineText.endsWith('/>')) {
        noChildTag = true;
      } else if (tagEnd.test(lineText)) {
        level++;
      } else if (tagStart.test(lineText)) {
        const tag = lineText.match(tagStart);

        if (noChildTag) {
          noChildTag = false;
        } else {
          if (level > 0) {
            level--;
          } else {
            return tag![1];
          }
        }
      }
      curLine--;
    }
  }
  return null;
};
