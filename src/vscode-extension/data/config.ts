import { workspace } from 'vscode';

export const preference: {
  prefix: RegExp;
  lang: string;
} = {
  prefix: /Ix/,
  lang: 'zh',
};

export const refreshPreference = () => {
  const configuration = getConfiguration();
  preference.prefix = new RegExp(`^(${configuration.get<string>('ComponentPrefix', '')})`, 'i');
  preference.lang = configuration.get<string>('Language', '');
};

export const getConfiguration = () => workspace.getConfiguration('IDuxCoder');
