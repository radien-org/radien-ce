export class GreetingElement extends HTMLElement {
  public static observedAttributes = [
    'title'
  ];

  attributeChangedCallback() {
    this.innerHTML = `<h1 style="color: red">Welcome to ${this.title}!</h1>`;
  }
}

customElements.define('happynrwl-greeting', GreetingElement);
