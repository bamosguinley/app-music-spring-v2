import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 7773,
  login: '-z',
};

export const sampleWithPartialData: IUser = {
  id: 4310,
  login: 'dZ@y8\\;GJC',
};

export const sampleWithFullData: IUser = {
  id: 25759,
  login: 'qxn2',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
