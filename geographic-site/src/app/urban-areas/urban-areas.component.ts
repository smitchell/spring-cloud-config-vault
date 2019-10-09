import { Component, OnInit } from '@angular/core';
import {GeographyService} from '../shared/geography-service/geography.service';
import {UrbanArea} from '../models/urban-area';

@Component({
  selector: 'app-vacation-list',
  templateUrl: './urban-areas.component.html',
  styleUrls: ['./urban-areas.component.scss']
})
export class UrbanAreasComponent implements OnInit {
  displayedColumns: string[] = ['geoId', 'name', 'areaLand', 'areaWater', 'landSqMiles', 'waterSqMiles'];

  urbanAreas: UrbanArea[];

  constructor(private geographyService: GeographyService) {
    console.log('UrbanAreasComponent constructor');
  }

  ngOnInit() {
    console.log('UrbanAreasComponent ngOnInit');
    this.geographyService.urbanAreas().subscribe(data => {
      console.log('UrbanAreasComponent return');
      if (data) {
        this.urbanAreas = data;
      } else {
        console.log('UrbanAreasComponent no data found');
      }
    });
  }

}
