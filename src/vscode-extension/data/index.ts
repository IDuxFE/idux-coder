import { readFile } from 'fs/promises';
import { MarkdownString, Uri } from 'vscode';
import { resolvePkgFolder } from '../utils';
import { getConfiguration } from './config';
const mergeObject = require('lodash.merge');
const forIn = require('lodash.forin');

export const apiMap: any = {};
export const initAPIMap = async () => {
  await mergeProjectAPIs();
  await mergeCustomAPIs();
  convertToMarkdown();
};

const convertToMarkdown = () => {
  const baseURL = getConfiguration().get('BaseURL', '');
  const baseURI = Uri.parse(baseURL);

  forIn(apiMap, (comp: any) => {
    forIn(comp, (lang: any) => {
      lang.raw = configureMarkdown(lang.raw, baseURI);
      forIn(lang, (type: any) => {
        // fix path problem
        if (type && typeof type === 'object') {
          forIn(type, (prop: any, key: string) => {
            type[key] = configureMarkdown(dataMarkdown(prop), Uri.parse(`${baseURL}/${lang.path ?? ''}`));
          });
        }
      });
    });
  });
};

const configureMarkdown = (markdown: string, baseUri: Uri) => {
  const markdownWrapper = new MarkdownString(markdown);
  markdownWrapper.supportHtml = true;
  markdownWrapper.supportThemeIcons = true;
  markdownWrapper.isTrusted = true;
  markdownWrapper.baseUri = baseUri;
  return markdownWrapper;
};

const dataWrapper = (data: string, tag: string) => (data !== '-' ? `*@${tag}*: ${data}\n\n` : '');

const dataMarkdown = (data: { description: string; type: string; default: string; globalConfig: string }) =>
  `
${data.description}

---

${dataWrapper(data.type, 'type')}
${dataWrapper(data.default, 'default')}
${dataWrapper(data.globalConfig, 'globalConfig')}
`;

const mergeProjectAPIs = async () => {
  const iduxFolders = await resolvePkgFolder([
    '@idux/cdk',
    '@idux/components',
    '@idux/pro',
    '@idux-vue2/cdk',
    '@idux-vue2/components',
    '@idux-vue2/pro',
  ]);
  return Promise.all(
    iduxFolders.filter(apiFile => apiFile).map(async apiFile => mergeObject(apiMap, await getJSON(apiFile!))),
  );
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
