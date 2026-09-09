import { Component, input } from '@angular/core';

export type ButtonVariant = 'primary' | 'secondary' | 'text';

@Component({
  selector: 'ui-button',
  templateUrl: './button.html',
  styleUrl: './button.scss',
})
export class Button {
  readonly variant = input<ButtonVariant>('primary');
  readonly type = input<'button' | 'submit'>('button');
  readonly disabled = input(false);
}
