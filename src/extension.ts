import { initAPIMap } from './data/index';
import { HoverProvider } from './providers/hover-provider';
import * as vscode from 'vscode';

export const extensionId = 'idux-coder';

const languageSelector = [
  {
    language: 'vue',
    scheme: '*',
  },
  {
    language: 'html',
    scheme: '*',
  },
  {
    language: 'javascriptreact',
    scheme: '*',
  },
  {
    language: 'typescriptreact',
    scheme: '*',
  },
];

export function activate(context: vscode.ExtensionContext) {
  console.log(`Extension "${extensionId}" is now active!`);

  const hoverRegistration = vscode.languages.registerHoverProvider(languageSelector, new HoverProvider());

  initAPIMap();

  // TODO: feat:watch node_modules change when install or update package
  context.subscriptions.push(hoverRegistration);
}

// this method is called when your extension is deactivated
export function deactivate() {
  console.log(`Extension "${extensionId}" is now deactivated!`);
}
