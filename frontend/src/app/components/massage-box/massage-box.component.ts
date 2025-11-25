import { Component, Input } from '@angular/core';
import { Massage } from '../../models/massage';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-massage-box',
  imports: [NgOptimizedImage],
  templateUrl: './massage-box.component.html',
  styleUrl: './massage-box.component.css'
})
export class MassageBoxComponent {
  @Input() massage: Massage | undefined;
}
