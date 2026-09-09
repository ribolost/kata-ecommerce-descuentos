import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import { DiscountCapAlert } from './discount-cap-alert';

describe('DiscountCapAlert', () => {
  it('should render the exact mandated 35% cap message when visible', () => {
    const fixture = TestBed.createComponent(DiscountCapAlert);
    fixture.componentRef.setInput('visible', true);
    fixture.detectChanges();

    const text = (fixture.nativeElement as HTMLElement).textContent?.trim();
    expect(text).toBe('¡Enhorabuena! Has alcanzado el límite máximo de ahorro permitido (35%)');
  });

  it('should render nothing when not visible', () => {
    const fixture = TestBed.createComponent(DiscountCapAlert);
    fixture.componentRef.setInput('visible', false);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent?.trim()).toBe('');
  });
});
