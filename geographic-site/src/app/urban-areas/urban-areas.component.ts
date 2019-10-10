import { Component, OnInit } from '@angular/core';
import {GeographyService} from '../shared/geography-service/geography.service';
import {UrbanArea} from '../models/urban-area';

@Component({
  selector: 'app-urban-areas',
  templateUrl: './urban-areas.component.html',
  styleUrls: ['./urban-areas.component.scss']
})
export class UrbanAreasComponent implements OnInit {
  displayedColumns: string[] = ['geoId', 'name', 'areaLand', 'areaWater', 'landSqMiles', 'waterSqMiles'];
  urbanAreas: UrbanArea[];
  page = 0;
  size = 15;
  totalElements: number;

  constructor(private geographyService: GeographyService) {
    console.log('UrbanAreasComponent constructor');
  }

  pageChanged(event): void {
    console.log(event);
    this.page = event.pageIndex;
    this.search();
  }

  ngOnInit() {
    console.log('UrbanAreasComponent ngOnInit');
    this.search();
  }

  search(): void {
    this.geographyService.urbanAreas(this.page, this.size).subscribe(data => {
      console.log('UrbanAreasComponent return');
      if (data) {
        this.urbanAreas = data._embedded.urbanAreas;
        this.totalElements = data.page.totalElements;
      } else {
        console.log('UrbanAreasComponent no data found');
      }
    });
  }

}
