import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeographyService {

  private host = 'http://localhost:9000';
  private geographyServiceUrl = this.host + '/geography-service';

  constructor(private http: HttpClient) {
  }

  static getStdOptions(): any {
    const headers: HttpHeaders = new HttpHeaders()
      .set('Content-Type', 'application/json;charset=UTF-8')
      .set('LANGUAGE', localStorage.getItem('locale'));
    return {
      headers: new HttpHeaders()
        .append('Content-Type', 'application/json;charset=UTF-8')
        .append('LANGUAGE', 'en-US')
    };
  }

  getUrbanArea(id: string): Observable<any> {
    const url = this.geographyServiceUrl + '/urbanAreas/' + id;

    return this.http
      .get(url, GeographyService.getStdOptions());
  }

  urbanAreas(page: number, size: number): Observable<any> {
    const url = this.geographyServiceUrl + '/urbanAreas?page=' + page + '&size=' + size + '&sort=name&name.dir=asc' ;
    return this.http
        .get(url, GeographyService.getStdOptions());
  }
}
