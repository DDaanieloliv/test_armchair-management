import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LayoutTopRightComponent } from './layout-top-right.component';

describe('LayoutTopRightComponent', () => {
  let component: LayoutTopRightComponent;
  let fixture: ComponentFixture<LayoutTopRightComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LayoutTopRightComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LayoutTopRightComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
