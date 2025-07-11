import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  toggled = false;
  _hasBackgroundImage = true;
  menus = [
    {
      title: 'Inicio',
      icon: 'fa-solid fa-house',
      active: false,
      type: 'simple',
      //badge: { //Para notificaciones
        //text: 'New ', //Texto de la notificacion
        //class: 'badge bg-warning text-dark' //Color de la notificacion
      //},
      //submenus: [{
        //title: 'Submenu 1',  //Submenu
      //}]
    },
    {
      title: 'Consejo Central',
      icon: 'fa-solid fa-flag',
      active: false,
      type: 'simple',
    },
    {
      title: 'Comité Regional',
      icon: 'fa-solid fa-map',
      active: false,
      type: 'simple',
    },
    {
      title: 'Comité Intermedio',
      icon: 'fa-solid fa-map',
      active: false,
      type: 'simple',
    },
    {
      title: 'Colectivo',
      icon: 'fa-solid fa-city',
      active: false,
      type: 'simple',
    },
    {
      title: 'Perfil',
      icon: 'fa fa-user',
      active: false,
      type: 'simple'
    },
    {
      title: 'Extra',
      type: 'header'
    },
    {
      title: 'Documentación',
      icon: 'fa fa-book',
      active: false,
      type: 'simple',
    }
  ];
  constructor() { /* TODO document why this constructor is empty */  }

  toggle() {
    this.toggled = ! this.toggled;
  }

  getSidebarState() {
    return this.toggled;
  }

  setSidebarState(state: boolean) {
    this.toggled = state;
  }

  getMenuList() {
    return this.menus;
  }

  get hasBackgroundImage() {
    return this._hasBackgroundImage;
  }

  set hasBackgroundImage(hasBackgroundImage) {
    this._hasBackgroundImage = hasBackgroundImage;
  }
}
