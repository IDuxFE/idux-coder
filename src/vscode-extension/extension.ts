import { initAPIMap } from './data';
import { HoverProvider } from './providers/hover-provider';
import * as vscode from 'vscode';
import { configurationRefresh } from './data/config';

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

  const configurationRegistration = vscode.workspace.onDidChangeConfiguration(e => {
    if (e.affectsConfiguration('IDuxCoder.ComponentPrefix') || e.affectsConfiguration('IDuxCoder.Language')) {
      configurationRefresh();
    } else if (
      e.affectsConfiguration('IDuxCoder.BaseURL') ||
      e.affectsConfiguration('IDuxCoder.CustomAPIs') ||
      e.affectsConfiguration('IDuxCoder.PackageLocation')
    ) {
      initAPIMap();
    }
  });

  initAPIMap();

  // TODO: feat:watch node_modules change when install or update package
  context.subscriptions.push(hoverRegistration, configurationRegistration);
}

// this method is called when your extension is deactivated
export function deactivate() {
  console.log(`Extension "${extensionId}" is now deactivated!`);
}
