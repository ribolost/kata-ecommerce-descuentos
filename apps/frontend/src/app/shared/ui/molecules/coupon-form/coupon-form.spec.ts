import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CouponForm } from './coupon-form';

describe('CouponForm', () => {
  let fixture: ComponentFixture<CouponForm>;
  let component: CouponForm;

  function submit(): void {
    const form = fixture.nativeElement.querySelector('form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit', { cancelable: true }));
    fixture.detectChanges();
  }

  function typeCode(value: string): void {
    const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }

  beforeEach(() => {
    fixture = TestBed.createComponent(CouponForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should not emit activate and should show a validation message when submitted empty', () => {
    const activateSpy = vi.fn();
    component.activate.subscribe(activateSpy);

    submit();

    expect(activateSpy).not.toHaveBeenCalled();
    expect(component.codeControl.touched).toBe(true);
    const message = fixture.nativeElement.querySelector('.coupon-form__message--error')?.textContent;
    expect(message).toContain('Ingresa un código de cupón');
  });

  it('should normalize the code (trim + uppercase) and emit it on a valid submit', () => {
    const activateSpy = vi.fn();
    component.activate.subscribe(activateSpy);

    typeCode('  welcome2026  ');
    submit();

    expect(activateSpy).toHaveBeenCalledWith('WELCOME2026');
  });

  it('should show the "not applied" message when notApplied is true and the field is otherwise valid', () => {
    typeCode('EXPIRED2024');
    fixture.componentRef.setInput('notApplied', true);
    fixture.detectChanges();

    const message = fixture.nativeElement.querySelector('.coupon-form__message--error')?.textContent;
    expect(message).toContain('El cupón no es válido, está inactivo o ya fue utilizado');
  });

  it('should prioritize the empty-field validation message over the "not applied" message', () => {
    fixture.componentRef.setInput('notApplied', true);
    submit();

    const message = fixture.nativeElement.querySelector('.coupon-form__message--error')?.textContent;
    expect(message).toContain('Ingresa un código de cupón');
  });

  it('should disable the input and submit button while pending', () => {
    fixture.componentRef.setInput('pending', true);
    fixture.detectChanges();

    const input = fixture.nativeElement.querySelector('input') as HTMLInputElement;
    const button = fixture.nativeElement.querySelector('button[type="submit"]') as HTMLButtonElement;

    expect(input.disabled).toBe(true);
    expect(button.disabled).toBe(true);
  });
});
