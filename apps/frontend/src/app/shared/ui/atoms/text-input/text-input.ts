import { Component, input, output } from '@angular/core';

@Component({
  selector: 'ui-text-input',
  templateUrl: './text-input.html',
  styleUrl: './text-input.scss',
})
export class TextInput {
  readonly value = input('');
  readonly placeholder = input('');
  readonly ariaLabel = input('');
  readonly disabled = input(false);
  readonly invalid = input(false);

  readonly valueChange = output<string>();

  onInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.valueChange.emit(target.value);
  }
}
