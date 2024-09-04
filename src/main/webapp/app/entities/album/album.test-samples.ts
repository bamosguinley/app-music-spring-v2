import { IAlbum, NewAlbum } from './album.model';

export const sampleWithRequiredData: IAlbum = {
  id: 31849,
};

export const sampleWithPartialData: IAlbum = {
  id: 31734,
  ref: 'ramener',
  title: 'euh avant que malade',
  description: 'volontiers géométrique',
};

export const sampleWithFullData: IAlbum = {
  id: 1738,
  ref: 'membre de l’équipe orange',
  author: 'carrément hi',
  title: "vlan à l'encontre de",
  description: 'suffisamment insolite',
  duration: 13245,
};

export const sampleWithNewData: NewAlbum = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
