import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { DiscountBreakdown } from '../../../../features/checkout/data-access/discount-breakdown.model';
import { DiscountBreakdownPanel } from './discount-breakdown-panel';

describe('DiscountBreakdownPanel', () => {
  let fixture: ComponentFixture<DiscountBreakdownPanel>;

  const breakdown: DiscountBreakdown = {
    categoryDiscountAmount: 5,
    volumeDiscountAmount: 6,
    couponDiscountAmount: 22.5,
    totalDiscountAmount: 33.5,
    effectiveDiscountPercentage: 0.22,
  };

  beforeEach(() => {
    fixture = TestBed.createComponent(DiscountBreakdownPanel);
    fixture.componentRef.setInput('breakdown', breakdown);
  });

  function lineLabels(): string[] {
    return Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('ui-discount-line-item')).map(
      (el) => el.textContent?.trim() ?? '',
    );
  }

  it('should render a line item only for each discount type present in appliedDiscounts', () => {
    fixture.componentRef.setInput('appliedDiscounts', ['CATEGORY', 'COUPON']);
    fixture.detectChanges();

    const lines = lineLabels();
    expect(lines.length).toBe(2);
    expect(lines.some((line) => line.includes('categoría'))).toBe(true);
    expect(lines.some((line) => line.includes('cupón'))).toBe(true);
    expect(lines.some((line) => line.includes('volumen'))).toBe(false);
  });

  it('should render all three lines when CATEGORY, VOLUME and COUPON are all applied', () => {
    fixture.componentRef.setInput('appliedDiscounts', ['CATEGORY', 'VOLUME', 'COUPON']);
    fixture.detectChanges();

    expect(lineLabels().length).toBe(3);
  });

  it('should render no lines when no discount type is applied', () => {
    fixture.componentRef.setInput('appliedDiscounts', []);
    fixture.detectChanges();

    expect(lineLabels().length).toBe(0);
  });
});
