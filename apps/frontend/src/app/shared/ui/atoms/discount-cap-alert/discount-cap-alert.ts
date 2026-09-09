import { Component, input } from '@angular/core';

@Component({
  selector: 'ui-discount-cap-alert',
  templateUrl: './discount-cap-alert.html',
  styleUrl: './discount-cap-alert.scss',
})
export class DiscountCapAlert {
  readonly visible = input(false);
}
