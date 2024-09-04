import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAlbum, NewAlbum } from '../album.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAlbum for edit and NewAlbumFormGroupInput for create.
 */
type AlbumFormGroupInput = IAlbum | PartialWithRequiredKeyOf<NewAlbum>;

type AlbumFormDefaults = Pick<NewAlbum, 'id'>;

type AlbumFormGroupContent = {
  id: FormControl<IAlbum['id'] | NewAlbum['id']>;
  ref: FormControl<IAlbum['ref']>;
  author: FormControl<IAlbum['author']>;
  title: FormControl<IAlbum['title']>;
  description: FormControl<IAlbum['description']>;
  duration: FormControl<IAlbum['duration']>;
};

export type AlbumFormGroup = FormGroup<AlbumFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AlbumFormService {
  createAlbumFormGroup(album: AlbumFormGroupInput = { id: null }): AlbumFormGroup {
    const albumRawValue = {
      ...this.getFormDefaults(),
      ...album,
    };
    return new FormGroup<AlbumFormGroupContent>({
      id: new FormControl(
        { value: albumRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      ref: new FormControl(albumRawValue.ref),
      author: new FormControl(albumRawValue.author),
      title: new FormControl(albumRawValue.title),
      description: new FormControl(albumRawValue.description),
      duration: new FormControl(albumRawValue.duration),
    });
  }

  getAlbum(form: AlbumFormGroup): IAlbum | NewAlbum {
    return form.getRawValue() as IAlbum | NewAlbum;
  }

  resetForm(form: AlbumFormGroup, album: AlbumFormGroupInput): void {
    const albumRawValue = { ...this.getFormDefaults(), ...album };
    form.reset(
      {
        ...albumRawValue,
        id: { value: albumRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AlbumFormDefaults {
    return {
      id: null,
    };
  }
}
