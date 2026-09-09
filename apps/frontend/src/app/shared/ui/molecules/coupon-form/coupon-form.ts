import { Component, input, output } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Button } from '../../atoms/button/button';
import { TextInput } from '../../atoms/text-input/text-input';

@Component({
  selector: 'ui-coupon-form',
  imports: [ReactiveFormsModule, TextInput, Button],
  templateUrl: './coupon-form.html',
  styleUrl: './coupon-form.scss',
})
export class CouponForm {
  readonly pending = input(false);
  readonly notApplied = input(false);

  readonly activate = output<string>();

  readonly codeControl = new FormControl('', { nonNullable: true, validators: [Validators.required] });

  onValueChange(value: string): void {
    this.codeControl.setValue(value);
  }

  onSubmit(): void {
    this.codeControl.markAsTouched();

    if (this.codeControl.invalid) {
      return;
    }

    const normalizedCode = this.codeControl.value.trim().toUpperCase();
    this.activate.emit(normalizedCode);
  }
}
