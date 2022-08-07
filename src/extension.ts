import { initAPIMap } from './data/index';
import { HoverProvider } from './providers/hover-provider';
import * as vscode from 'vscode';

export const extensionId = 'idux-coder';

export function activate(context: vscode.ExtensionContext) {
  console.log(`Extension "${extensionId}" is now active!`);

  const hoverRegistration = vscode.languages.registerHoverProvider(
    [
      {
        language: 'vue',
        scheme: 'file',
      },
      {
        language: 'vue',
        scheme: 'untitled',
      },
      {
        language: 'html',
        scheme: 'file',
      },
      {
        language: 'html',
        scheme: 'untitled',
      },
    ],
    new HoverProvider(),
  );

  initAPIMap();

  // TODO: feat:watch node_modules change when install or update package

  context.subscriptions.push(hoverRegistration);
}

// this method is called when your extension is deactivated
export function deactivate() {
  console.log(`Extension "${extensionId}" is now deactivated!`);
}
