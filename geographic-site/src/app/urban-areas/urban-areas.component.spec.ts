import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UrbanAreasComponent } from './urban-areas.component';

describe('VactionListComponent', () => {
  let component: UrbanAreasComponent;
  let fixture: ComponentFixture<UrbanAreasComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UrbanAreasComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UrbanAreasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
