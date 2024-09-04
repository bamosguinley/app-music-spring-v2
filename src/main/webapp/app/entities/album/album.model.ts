export interface IAlbum {
  id: number;
  ref?: string | null;
  author?: string | null;
  title?: string | null;
  description?: string | null;
  duration?: number | null;
}

export type NewAlbum = Omit<IAlbum, 'id'> & { id: null };
