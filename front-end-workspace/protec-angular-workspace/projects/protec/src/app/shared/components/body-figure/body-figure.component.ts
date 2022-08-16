import { Component, ViewChild, ElementRef, AfterViewInit, OnInit } from '@angular/core';
import { StorageService } from '../../../shared/services/storage/storage.service';
import { LOCAL } from '../../../shared/services/storage/local.enum';
import { Router } from '@angular/router';

@Component({
  selector: 'app-body-figure',
  templateUrl: './body-figure.component.html',
  styleUrls: ['./body-figure.component.scss']
})
export class BodyFigureComponent implements OnInit {

  //@ViewChild('bodyCanvas')
  //private bodyCanvas: ElementRef = {} as ElementRef;
  //context = CanvasRenderingContext2D;

  constructor(private readonly storageService: StorageService, private readonly router: Router) { }

  ngAfterViewInit(): void {
    //this.context = this.bodyCanvas.nativeElement.getContext('2d');
    //this.buildCanvas();
  }

  ngOnInit(): void {
  }

  selectBodyPart(part:string) {
    this.storageService.setItem(LOCAL.BODY_PART, part);
    this.router.navigate(['/data-acquisition/part-body']);
  }

  buildCanvas() {

    let canvas: any = document.getElementById('bodyCanvas');
    if (canvas!.getContext) {
      let context = canvas.getContext('2d');
      let base_image = new Image();
      base_image.src = '/assets/images/body/ProTec_Software_Figure_pa-head.svg';
      //this.context.drawImage(base_image, 100, 100);
      //context.drawImage(base_image, 100, 100);
      context.clearRect(0, 0, canvas.width, canvas.height);
      base_image.onload = function(){
        const w = base_image.width, h = base_image.height;
        context.drawImage(base_image, 0, 0, 30, 30);
        //context.drawImage(base_image, 0, 0, 250, 150);
        //context.imageSmoothingEnabled = true;
        //context.drawImage(base_image, 0, 0, w * 2, h * 2);
        //context.drawImage(base_image, 0, 0, 250, 150);
      }
    }

  }

}
