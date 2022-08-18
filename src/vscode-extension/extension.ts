import { initAPIMap } from './data';
import { HoverProvider } from './providers/hover-provider';
import * as vscode from 'vscode';
import { refreshPreference } from './data/config';

export const extensionId = 'idux-coder';

const languageSelector: vscode.DocumentSelector = [
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
const rebuildConfigs = ['IDuxCoder.BaseURL', 'IDuxCoder.CustomAPIs', 'IDuxCoder.PackageLocation'];
const preferConfigs = ['IDuxCoder.ComponentPrefix', 'IDuxCoder.Language'];

export function activate(context: vscode.ExtensionContext) {
  console.log(`Extension "${extensionId}" is now active!`);

  const hoverRegistration = vscode.languages.registerHoverProvider(languageSelector, new HoverProvider());

  const configurationRegistration = vscode.workspace.onDidChangeConfiguration(e => {
    if (preferConfigs.find(item => e.affectsConfiguration(item))) {
      refreshPreference();
    } else if (rebuildConfigs.find(item => e.affectsConfiguration(item))) {
      initAPIMap();
    }
  });

  refreshPreference();
  initAPIMap();

  // TODO: feat:watch node_modules change when install or update package
  context.subscriptions.push(hoverRegistration, configurationRegistration);
}

// this method is called when your extension is deactivated
export function deactivate() {
  console.log(`Extension "${extensionId}" is now deactivated!`);
}
