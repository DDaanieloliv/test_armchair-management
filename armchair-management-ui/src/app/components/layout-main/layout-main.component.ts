import { Component } from '@angular/core';
import { DecorativeBarrComponent } from '../decorative-barr/decorative-barr.component'
import { DecorativePretendBackgroundComponent } from '../decorative-pretend-background/decorative-pretend-background.component';

@Component({
  selector: 'app-layout-main',
  standalone: true,
  imports: [DecorativeBarrComponent, DecorativePretendBackgroundComponent],
  templateUrl: './layout-main.component.html',
  styleUrl: './layout-main.component.css'
})
export class LayoutMainComponent {

}
