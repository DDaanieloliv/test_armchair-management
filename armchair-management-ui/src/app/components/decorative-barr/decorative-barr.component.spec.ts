import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecorativeBarrComponent } from './decorative-barr.component';

describe('DecorativeBarrComponent', () => {
  let component: DecorativeBarrComponent;
  let fixture: ComponentFixture<DecorativeBarrComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecorativeBarrComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DecorativeBarrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
