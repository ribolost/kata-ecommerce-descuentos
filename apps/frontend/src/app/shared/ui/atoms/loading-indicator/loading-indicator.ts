import { Component, input } from '@angular/core';

@Component({
  selector: 'ui-loading-indicator',
  templateUrl: './loading-indicator.html',
  styleUrl: './loading-indicator.scss',
})
export class LoadingIndicator {
  readonly label = input('Cargando');
}
