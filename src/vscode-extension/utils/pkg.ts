import { getConfiguration } from '../data/config';
import { join } from 'path';
import { workspace, window } from 'vscode';
import { access } from 'fs/promises';

const nodeModulesName = 'node_modules';

export const resolvePkgFolder = async (pkgName: string[]) => {
  let nodeModulesPath = getConfiguration().get<string>('PackageLocation')!;
  const workspacePath =await getWorkspaceFolder();

  if (nodeModulesPath === '') {
    getConfiguration().update('PackageLocation', nodeModulesName, false);
  }

  nodeModulesPath = join(workspacePath!, nodeModulesPath);

  if (await exists(nodeModulesPath)) {
    return Promise.all(
      pkgName.map(async name => {
        const apiLoc = join(nodeModulesPath, name, 'api.json');
        if (await exists(apiLoc)) {
          return apiLoc;
        } else {
          return undefined;
        }
      }),
    );
  } else {
    return [];
  }
};

const exists = async (path: string) => {
  try {
    await access(path);
    return true;
  } catch {
    return false;
  }
};

const getWorkspaceFolder = async () => {
  // If in a multifolder workspace, prompt user to select which one to traverse.
  if (workspace.workspaceFolders!.length <= 1) {
    // Otherwise, use the first one
    return workspace.workspaceFolders![0].uri.fsPath;
  }

  const selected = await window.showQuickPick(
    workspace.workspaceFolders!.map(folder => ({
      label: folder.name,
      folder,
    })),
    {
      placeHolder: 'Select workspace folder',
    },
  );

  if (!selected) {
    return;
  }
  return selected.folder.uri.fsPath;
};
