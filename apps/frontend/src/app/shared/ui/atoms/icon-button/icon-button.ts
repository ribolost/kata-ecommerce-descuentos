import { Component, input } from '@angular/core';

@Component({
  selector: 'ui-icon-button',
  templateUrl: './icon-button.html',
  styleUrl: './icon-button.scss',
})
export class IconButton {
  readonly ariaLabel = input.required<string>();
  readonly disabled = input(false);
}
