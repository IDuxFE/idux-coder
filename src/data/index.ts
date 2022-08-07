import { readFile } from 'fs/promises';
import { MarkdownString, Uri } from 'vscode';
import { getConfiguration, resovlePkgFloder } from '../utils';
const mergeObject = require('lodash.merge');
const forIn = require('lodash.forin');

export const apiMap: any = {};
let baseURI: Uri;

export const initAPIMap = async () => {
  await mergeProjectAPIs();
  await mergeCustomAPIs();
  convertToMarkdown();
};

const convertToMarkdown = () => {
  baseURI = Uri.parse(getConfiguration().get('BaseURL', ''));

  forIn(apiMap, (comp: any) => {
    forIn(comp, (lang: any) => {
      lang.raw = configureMarkdown(lang.raw);
      forIn(lang, (type: any) => {
        forIn(type, (prop: any, key: any) => {
          type[key] = configureMarkdown(dataMarkdown(prop));
        });
      });
    });
  });
};

const configureMarkdown = (markdown: string) => {
  const markdownWrapper = new MarkdownString(markdown);
  markdownWrapper.supportHtml = true;
  markdownWrapper.supportThemeIcons = true;
  markdownWrapper.baseUri = baseURI;
  return markdownWrapper;
};

const dataWrapper = (data: string, tag: string) => (data !== '-' ? `*@${tag}*: ${data}\n\n` : '');

const dataMarkdown = (data: { description: any; type: string; default: string; globalConfig: string }) =>
  `
${data.description}

---

${dataWrapper(data.type, 'type')}
${dataWrapper(data.default, 'default')}
${dataWrapper(data.globalConfig, 'globalConfig')}
`;

const mergeProjectAPIs = async () => {
  const iduxFolders = await resovlePkgFloder(['@idux/cdk', '@idux/components', '@idux/pro']);
  iduxFolders.forEach(async apiFile => apiFile && mergeObject(apiMap, await getJSON(apiFile)));
};

const mergeCustomAPIs = async () => {
  const customAPIs = getConfiguration().get<string>('CustomAPIs');
  if (customAPIs && customAPIs !== '') {
    mergeObject(apiMap, await getJSON(customAPIs));
  }
};

const getJSON = async (file: string): Promise<Object> => {
  try {
    return JSON.parse(await readFile(file, 'utf-8'));
  } catch {
    return {};
  }
};
