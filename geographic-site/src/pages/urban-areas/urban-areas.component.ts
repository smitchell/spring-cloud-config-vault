import { Component, OnInit } from '@angular/core';
import {GeographyService} from '../../app/shared/geography-service/geography.service';
import {UrbanArea} from '../../app/models/urban-area';
import {Subject} from 'rxjs';
import {debounceTime, distinctUntilChanged} from 'rxjs/operators';

@Component({
  selector: 'app-urban-areas-page',
  templateUrl: './urban-areas.component.html',
  styleUrls: ['./urban-areas.component.scss']
})
export class UrbanAreasComponent implements OnInit {
  displayedColumns: string[] = ['geoId', 'name', 'areaLand', 'areaWater', 'landSqMiles', 'waterSqMiles'];
  urbanAreas: UrbanArea[];
  userSearchUpdate = new Subject<string>();
  keyword: string;
  page = 0;
  size = 7;
  totalElements: number;
  totalPages = 0;

  constructor(private geographyService: GeographyService) {
    console.log('UrbanAreasComponent constructor');
    // Debounce search.
    this.userSearchUpdate.pipe(
        debounceTime(400),
        distinctUntilChanged())
        .subscribe(value => {
          this.searchByName(value);
        });
  }

  pageChanged(event): void {
    this.page = event.pageIndex;
    this.size = event.pageSize;
    if (this.keyword && this.keyword.length > 1) {
      this.searchByName(this.keyword);
    } else {
      this.search();
    }
  }

  ngOnInit(): void {
    this.search();
  }

  searchByName(searchTerm: any): void {
    if (!searchTerm || searchTerm.length === 0) {
      this.search();
    } else if (searchTerm && searchTerm.length > 1) {
      this.page = 0;
      this.geographyService.searchUrbanAreasByName(searchTerm, this.page, this.size).subscribe(data => {
        if (data) {
          this.urbanAreas = data._embedded.urbanAreas;
          this.totalElements = data.page.totalElements;
          this.totalPages = data.page.totalPages;
        } else {
          console.log('UrbanAreasComponent no data found');
        }
      });
    }
  }

  search(): void {
    this.geographyService.urbanAreas(this.page, this.size).subscribe(data => {
      if (data) {
        this.urbanAreas = data._embedded.urbanAreas;
        this.totalElements = data.page.totalElements;
        this.totalPages = data.page.totalPages;
      } else {
        console.log('UrbanAreasComponent no data found');
      }
    });
  }

}
