// TODO: perf problem

import { workspace } from 'vscode';

export const configuration = () => {
  const configuration = getConfiguration();
  // memo
  const prefix = new RegExp(`^(${configuration.get<string>('ComponentPrefix', '')})`, 'i');
  const lang = configuration.get<string>('Language', '');

  return {
    prefix,
    lang,
  };
};

export const getConfiguration = () => workspace.getConfiguration('IDuxCoder');
