import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecorativePretendBackgroundComponent } from './decorative-pretend-background.component';

describe('DecorativePretendBackgroundComponent', () => {
  let component: DecorativePretendBackgroundComponent;
  let fixture: ComponentFixture<DecorativePretendBackgroundComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecorativePretendBackgroundComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecorativePretendBackgroundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
