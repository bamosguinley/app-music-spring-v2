import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'd3350cfa-9479-4996-9106-b93643815b84',
};

export const sampleWithPartialData: IAuthority = {
  name: '86b0f2f5-58a9-400b-b7f9-ad3ebdcef97c',
};

export const sampleWithFullData: IAuthority = {
  name: 'b7245904-aabd-4d85-821c-6bf923b3a6dc',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
