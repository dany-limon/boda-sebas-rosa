import React, { Component } from 'react'
import './App.css'
import firebase from 'firebase'
import Gallery from 'react-photo-gallery'
import _ from 'lodash'
import FontAwesome from 'react-fontawesome'
import {Animated} from "react-animated-css"

export default class extends Component {

  constructor(){
    super()

    this.state = {
      images: null,
      index: -1,
      showGrid: false,
      pause: false,
      isVisible:true,
    }
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.resize)
  }

  componentDidMount(){

    window.addEventListener('resize', this.resize)
    
    firebase.auth().signInWithEmailAndPassword('admin@admin.com', 'admin12345678')
    .then(()=>{
      var starCountRef = firebase.database().ref('images/')
      starCountRef.on('value', snapshot => {
        this.startUpdates(this.state.images ? 30000 : 10000 )
        const images = _.map(snapshot.val(), item => item )
        this.setState({images}, ()=>{
          this.updateIndex(images.length-1)
        })
      })
    })
  }

  getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
  }
  
  startUpdates(time=10000){
    clearTimeout(this.currentTimeOut)
    this.currentTimeOut = setTimeout(()=>{
      this.nextImage()
    },time)
  }

  resize = () => this.forceUpdate()

  openImage = src => window.open(src)

  updateIndex(index){
    this.setState({isVisible:false})
    setTimeout(()=>{
      this.setState({index}, ()=>{
        setTimeout(()=>{
          this.setState({isVisible:true})
        },500)
      })
    },1000)
  }

  nextImage(){
    clearTimeout(this.currentTimeOut)
    let index = this.state.index
      const { images } = this.state
      if (images && images.length && index<images.length-1){
        index = index + 1 //this.getRandomInt(0, images.length)
      }else{
        index = 0
      }
      if (!this.state.pause){
        this.updateIndex(index)
      }
      this.startUpdates()
  }

  previousImage(){
    clearTimeout(this.currentTimeOut)
    let index = this.state.index
      const { images } = this.state
      if (images && images.length && index>0){
        index = index - 1
      }else{
        index = images && images.length ? images.length-1 : 0
      }
      if (!this.state.pause){
        this.updateIndex(index)
      }
      this.startUpdates()
  }

  renderImages(){
    const { index, images, isVisible } = this.state
    if (images && images.length>index && index>=0){
      const item = images[index]
      return(
        <div className="photo" >
          
          {this.state.images.length>0 &&
          <Animated animationIn="fadeInUp" animationOut="bounceOut" animationInDelay={500} isVisible={isVisible}>
            <img 
              alt='image_alt'
              onClick={()=>this.setState({pause:!this.state.pause})} 
              src={item.src} 
            />
          </Animated>
          }

          {this.state.pause && 
            <div className='pause'  onClick={()=>this.setState({pause:true})} >
              <FontAwesome
                name='pause'
                size='2x'
              />
            </div> 
          }
          <div className='previous' onClick={()=>this.previousImage()} >
            <Animated animationIn="bounceInLeft" animationInDelay={1500}>
              <FontAwesome
                name='angle-left'
                size='5x'
              />
            </Animated>
          </div> 
          
          <div className='next' onClick={()=>this.nextImage()} >
            <Animated animationIn="bounceInRight" animationInDelay={1500}>
              <FontAwesome
                name='angle-right'
                size='5x'
              />
            </Animated>
          </div> 
        </div>
      )
    }
  }

  renderGallery(){
    return(
      <div className='grid-gallery'>
        <Gallery 
          photos={this.state.images}
          onClick={(e, o)=>this.openImage(o.photo.src)}
        />
      </div>
    )
  }

  renderBody(){
    return this.state.showGrid ? this.renderGallery() : this.renderImages()
  }

  renderHeader(){
    return(
      <Header 
        onClickRight = {()=>this.setState({showGrid:!this.state.showGrid})}
        rightIconName = {this.state.showGrid ? 'play' : 'th'}
        title= 'Sebas & Rosa'
        subtitle= '24 de Marzo de 2018'
      />
    )
  }

  render() {

    return (
      <div className="App">
        { this.renderHeader() }
        { this.state.images && this.renderBody()}
      </div>
    )
  }
}

class Header extends Component {
  
  static defaultProps = {
    title: '',
    subtitle: '',
    showRight: true,
    rightIconName: '',
    onClickRight:()=>{},
  }

  render(){
    
    const { title, subtitle, showRight, onClickRight, rightIconName } = this.props
    
    return(
      <header>
        
          <h1><Animated animationIn="bounceInDown" animationInDelay={250}>{title}</Animated></h1>
          <h2><Animated animationIn="bounceInDown" animationInDelay={300}>{subtitle}</Animated></h2>
        {showRight &&
        <div className='right-icon' onClick={onClickRight.bind(this)} >
          <Animated animationIn="bounceInRight" animationInDelay={1000}>
            <FontAwesome
              name={rightIconName}
              size='2x'
            />
          </Animated>
        </div> }
      </header>
    )
  }
}