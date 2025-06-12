import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutBottomRightComponent } from './layout-bottom-right.component';

describe('LayoutBottomRightComponent', () => {
  let component: LayoutBottomRightComponent;
  let fixture: ComponentFixture<LayoutBottomRightComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutBottomRightComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutBottomRightComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
