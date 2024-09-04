import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IAlbum } from '../album.model';
import { AlbumService } from '../service/album.service';
import { AlbumFormGroup, AlbumFormService } from './album-form.service';

@Component({
  standalone: true,
  selector: 'jhi-album-update',
  templateUrl: './album-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AlbumUpdateComponent implements OnInit {
  isSaving = false;
  album: IAlbum | null = null;

  protected albumService = inject(AlbumService);
  protected albumFormService = inject(AlbumFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AlbumFormGroup = this.albumFormService.createAlbumFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ album }) => {
      this.album = album;
      if (album) {
        this.updateForm(album);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const album = this.albumFormService.getAlbum(this.editForm);
    if (album.id !== null) {
      this.subscribeToSaveResponse(this.albumService.update(album));
    } else {
      this.subscribeToSaveResponse(this.albumService.create(album));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAlbum>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(album: IAlbum): void {
    this.album = album;
    this.albumFormService.resetForm(this.editForm, album);
  }
}
