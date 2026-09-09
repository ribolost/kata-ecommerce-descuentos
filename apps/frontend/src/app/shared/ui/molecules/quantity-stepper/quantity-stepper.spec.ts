import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { QuantityStepper } from './quantity-stepper';

describe('QuantityStepper', () => {
  let fixture: ComponentFixture<QuantityStepper>;

  beforeEach(() => {
    fixture = TestBed.createComponent(QuantityStepper);
  });

  function buttons(): NodeListOf<HTMLButtonElement> {
    return (fixture.nativeElement as HTMLElement).querySelectorAll('button');
  }

  it('should render only the increment control when quantity is 0 (product not in cart)', () => {
    fixture.componentRef.setInput('quantity', 0);
    fixture.componentRef.setInput('max', 5);
    fixture.detectChanges();

    expect(buttons().length).toBe(1);
    expect(buttons()[0].getAttribute('aria-label')).toBe('Agregar al carrito');
    expect((fixture.nativeElement as HTMLElement).querySelector('.quantity-stepper--single')).not.toBeNull();
  });

  it('should render decrement, value and increment controls once a quantity is present', () => {
    fixture.componentRef.setInput('quantity', 2);
    fixture.componentRef.setInput('max', 5);
    fixture.detectChanges();

    expect(buttons().length).toBe(2);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('2');
  });

  it('should disable the increment control once the quantity reaches the stock ceiling', () => {
    fixture.componentRef.setInput('quantity', 5);
    fixture.componentRef.setInput('max', 5);
    fixture.detectChanges();

    const incrementButton = buttons()[buttons().length - 1];
    expect(incrementButton.disabled).toBe(true);
  });

  it('should emit increment when the increment control is clicked', () => {
    fixture.componentRef.setInput('quantity', 0);
    fixture.componentRef.setInput('max', 5);
    fixture.detectChanges();

    const incrementSpy = vi.fn();
    fixture.componentInstance.increment.subscribe(incrementSpy);

    buttons()[0].click();

    expect(incrementSpy).toHaveBeenCalled();
  });

  it('should emit decrement when the decrement control is clicked', () => {
    fixture.componentRef.setInput('quantity', 1);
    fixture.componentRef.setInput('max', 5);
    fixture.detectChanges();

    const decrementSpy = vi.fn();
    fixture.componentInstance.decrement.subscribe(decrementSpy);

    buttons()[0].click();

    expect(decrementSpy).toHaveBeenCalled();
  });
});
