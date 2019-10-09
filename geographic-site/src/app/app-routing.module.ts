import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {UrbanAreasComponent} from './urban-areas/urban-areas.component';

const routes: Routes = [
  {path: 'urbanAreas', component: UrbanAreasComponent}
  ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
