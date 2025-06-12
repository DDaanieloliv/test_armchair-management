import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutBottomLeftComponent } from './layout-bottom-left.component';

describe('LayoutBottomLeftComponent', () => {
  let component: LayoutBottomLeftComponent;
  let fixture: ComponentFixture<LayoutBottomLeftComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutBottomLeftComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutBottomLeftComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
